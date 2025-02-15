import subprocess
import time

subprocess.Popen(["open", "-a", "Terminal", "--args", "zsh", "-c", "python3 ./white.py"])
time.sleep(1.5)
subprocess.Popen(["open", "-a", "Terminal", "--args", "zsh", "-c", "python3 ./black.py"])
time.sleep(1.5)
subprocess.Popen(["open", "-a", "Terminal", "--args", "zsh", "-c", "python3 ./spettatore.py"])
