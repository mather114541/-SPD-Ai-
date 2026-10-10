# -*- coding: utf-8 -*-
"""观测 v2（全层已探自动地图）共用的常量、数据读取、通道展开与 CNN 模型。"""
import os
import struct

import numpy as np
import torch
import torch.nn as nn

MAPW = 48
MAPH = 48
NMAP = MAPW * MAPH          # 2304
NCH = 16                    # 位域展开后的通道数
N_SCALAR = 105              # 96 + 战斗态势 9 维
N_ACT = 9                   # 宏意图：探索/下楼/攻击/喝药/吃饭/搜索/拾取/等待/投掷

B_KNOWN, B_PASSABLE, B_WALL, B_DOOR, B_AWARE, B_GRASS, B_WATER = 0, 1, 2, 3, 4, 5, 6
B_FOV, B_MOB, B_ITEM, B_TRAP, B_FRONTIER, B_HERO = 7, 8, 9, 10, 11, 12
B_ENTRANCE, B_EXIT, B_MOB_FAST = 13, 14, 15
M_EXPLORE, M_EXIT, M_FIGHT, M_HEAL, M_EAT, M_SEARCH, M_PICKUP, M_WAIT = range(8)

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAR = r"C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
HB = os.path.join(BASE, "lib", "gdx-backend-headless-1.14.0.jar")
CLS = os.path.join(BASE, "build", "classes")
CLASSPATH = ";".join([CLS, JAR, HB])
OUTDIR = os.path.join(BASE, "rl-train", "out")


def read_bin(path):
    """返回结构化 memmap。字段：map (N,NMAP) uint16, sc (N,N_SCALAR) int32, act (N,) int32"""
    with open(path, "rb") as f:
        hdr = f.read(32)
    magic, mw, mh, nsc, nact = struct.unpack(">4siiii", hdr[:20])
    if magic != b"SPD2":
        raise RuntimeError("bad magic %r in %s" % (magic, path))
    if (mw, mh, nsc) != (MAPW, MAPH, N_SCALAR):
        raise RuntimeError("shape mismatch %s: %s" % (path, (mw, mh, nsc)))
    rec = np.dtype([("map", ">u2", (NMAP,)), ("sc", ">i4", (nsc,)), ("act", ">i4")])
    return np.memmap(path, dtype=rec, mode="r", offset=32)


def obs_to_map(obs):
    """obs: (N, 2400) int -> (N, NMAP) uint16 map, (N, N_SCALAR) int32 scalars."""
    obs = np.asarray(obs)
    return obs[:, :NMAP].astype(np.uint16), obs[:, NMAP:].astype(np.int32)


def expand(m):
    """m: (B, NMAP) 整数位域 -> (B, NCH, MAPH, MAPW) float32"""
    m = np.asarray(m).astype(np.int64, copy=False)
    chans = [((m >> b) & 1).astype(np.float32) for b in range(NCH)]
    x = np.stack(chans, axis=1)
    return x.reshape(-1, NCH, MAPH, MAPW)


def norm_scalars(sc, mean, std):
    return ((sc.astype(np.float32) - mean) / std).astype(np.float32)


class CNN(nn.Module):
    def __init__(self):
        super().__init__()
        self.conv = nn.Sequential(
            nn.Conv2d(NCH, 32, 3, padding=1), nn.ReLU(),
            nn.Conv2d(32, 32, 3, padding=1), nn.ReLU(),
            nn.Conv2d(32, 64, 3, stride=2, padding=1), nn.ReLU(),
            nn.Conv2d(64, 64, 3, stride=2, padding=1), nn.ReLU(),
        )
        # 48x48 -> 24x24 -> 12x12，保留空间结构（导航需要方向感，不做激进池化）
        self.fc_map = nn.Sequential(nn.Flatten(), nn.Linear(64 * 12 * 12, 512), nn.ReLU())
        self.fc_sc = nn.Sequential(nn.Linear(N_SCALAR, 64), nn.ReLU())
        self.trunk = nn.Sequential(nn.Linear(512 + 64, 256), nn.ReLU())
        self.actor = nn.Linear(256, N_ACT)
        self.critic = nn.Linear(256, 1)

    def forward(self, m, s):
        h = self.conv(m).flatten(1)
        h = self.fc_map(h)
        s = self.fc_sc(s)
        z = self.trunk(torch.cat([h, s], dim=1))
        return self.actor(z), self.critic(z)


def save_ckpt(path, model, mean, std, meta=None):
    torch.save({"state": model.state_dict(), "mean": mean, "std": std, "meta": meta or {}}, path)


def load_ckpt(path, device="cpu"):
    ck = torch.load(path, map_location=device, weights_only=False)
    model = CNN().to(device)
    model.load_state_dict(ck["state"])
    model.eval()
    return model, ck["mean"], ck["std"], ck.get("meta", {})
