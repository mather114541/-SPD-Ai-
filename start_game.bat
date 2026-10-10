@echo off
setlocal
cd /d "%~dp0"
title Shattered Pixel Dungeon - AI Autopilot v0.0.1

set "MODEL=%~1"
rem default model: prefer bc11 (trained with pickup). pass a path as arg 1 to override.
if "%MODEL%"=="" set "MODEL=rl-train/out/bc11.onnx"
set "STEPMS=%~2"
if "%STEPMS%"=="" set "STEPMS=500"

set "JAR=%CD%\ShatteredPD-v4.0.0-Java.jar"
if not exist "%JAR%" set "JAR=%USERPROFILE%\Downloads\ShatteredPD-v4.0.0-Java.jar"
if not exist "%JAR%" goto nojar
if not exist "%CD%\build\classes" goto nobody

echo Starting... (click the AI button in the toolbar, or press F8, to toggle autopilot)
echo Model: %MODEL%   Pace: %STEPMS% ms/step
if "%MODEL%"=="" (
  java -Xmx1g -Drl.stepMs=%STEPMS% -cp "build\classes;%JAR%;lib\onnxruntime-1.17.3.jar" com.shatteredpixel.shatteredpixeldungeon.desktop.DesktopLauncher
) else (
  java -Xmx1g -Drl.stepMs=%STEPMS% -Drl.model=%MODEL% -cp "build\classes;%JAR%;lib\onnxruntime-1.17.3.jar" com.shatteredpixel.shatteredpixeldungeon.desktop.DesktopLauncher
)
echo Game exited (exit=%ERRORLEVEL%)
pause
exit /b 0

:nojar
echo [ERROR] ShatteredPD-v4.0.0-Java.jar not found.
echo         Put it in this folder, or edit JAR path in this file.
pause
exit /b 1

:nobody
echo [ERROR] build\classes not found (compiled game missing).
pause
exit /b 1
