# 生成含检索隐藏门/陷阱行为的轨迹数据（bc6 用）。8 进程并行，各自独立 cwd 隔离 savedata。
$root = "C:\Users\33040\Doubao\chats\2026-10-08\new-chat"
$jar = "C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
$hb  = "$root\lib\gdx-backend-headless-1.14.0.jar"
$cp  = "$root\build\classes;$jar;$hb"
New-Item -ItemType Directory -Force -Path "$root\build\gen" | Out-Null
for ($i = 0; $i -lt 8; $i++) {
    $wd = "$root\build\envs\gen7_$i"
    New-Item -ItemType Directory -Force -Path $wd | Out-Null
    $out = "$root\rl-train\out\traj7_${i}.bin"
    $seed = 31000000 + $i * 100000
    $stdout = "$root\build\gen\g7_${i}.out"
    $stderr = "$root\build\gen\g7_${i}.err"
    Start-Process -FilePath "java" -ArgumentList @("-Xmx512m", "-cp", $cp, "rl.RLGenDataBin",
        "--out", $out, "--per", "150", "--max", "2500", "--target", "5", "--mindepth", "2",
        "--seedbase", $seed.ToString()) `
        -WorkingDirectory $wd -WindowStyle Hidden `
        -RedirectStandardOutput $stdout `
        -RedirectStandardError $stderr
    Write-Output ("spawned gen7_$i seed=$seed out=$out")
}
Write-Output "ALL 8 SPAWNED"
