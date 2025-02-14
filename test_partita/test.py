import subprocess
import time

subprocess.Popen("start cmd /k python ./white.py", shell=True)
time.sleep(1.5)
subprocess.Popen("start cmd /k python ./black.py", shell=True)
time.sleep(1.5)
subprocess.Popen("start cmd /k python ./spettatore.py", shell=True)