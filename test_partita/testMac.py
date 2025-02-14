import subprocess
import time

subprocess.Popen(["open", "-a", "Terminal", "python3", "./white.py"])
time.sleep(1.5)
subprocess.Popen(["open", "-a", "Terminal", "python3", "./black.py"])
time.sleep(1.5)
subprocess.Popen(["open", "-a", "Terminal", "python3", "./spettatore.py"])