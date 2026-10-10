"""把 bc checkpoint 导出为 ONNX + mean/std（供游戏内 Java ONNX Runtime 推理）。
用法: python export_onnx.py <bc.pt> <out.onnx> [out.norm.json]
"""
import sys, json, torch, numpy as np
from spd_v2 import load_ckpt, NMAP, N_SCALAR, N_ACT, MAPH, MAPW

def main():
    ck = sys.argv[1]
    out = sys.argv[2]
    normf = sys.argv[3] if len(sys.argv) > 3 else out.replace(".onnx", ".norm.json")
    model, mean, std, meta = load_ckpt(ck)
    model.eval()
    dummy_m = torch.zeros(1, 16, MAPH, MAPW)
    dummy_s = torch.zeros(1, N_SCALAR)
    with torch.no_grad():
        torch.onnx.export(model, (dummy_m, dummy_s), out,
                          input_names=["m", "s"], output_names=["actor", "critic"],
                          opset_version=13, dynamo=False)
    mean = np.asarray(mean, dtype=np.float64)
    std = np.asarray(std, dtype=np.float64)
    json.dump({"mean": mean.tolist(), "std": std.tolist()}, open(normf, "w"))
    print("ONNX saved: %s (%d bytes)" % (out, __import__("os").path.getsize(out)))
    print("norm saved: %s (%d scalars)" % (normf, len(mean)))

if __name__ == "__main__":
    main()
