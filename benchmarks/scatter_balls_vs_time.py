import toml
import json
import subprocess
import numpy as np
import matplotlib.pyplot as plt
import math

def main() -> None:
  with open('out/times.json', 'r') as file:
    times = json.load(file)
  
  y_positions = ["42.0", "49.368421052631575", "56.0"]


  for y in y_positions:
    ball_amt = list(reversed(times[y].keys()))
    time = list(reversed(times[y].values()))
    plt.plot(time, ball_amt, label=f"y={round(float(y), 2)}", marker='o', markersize=5)

  plt.xlabel("Tiempo (s)", fontsize=20)
  plt.ylabel("Bolas restantes", fontsize=20)

  plt.grid()
  plt.tight_layout()
  plt.legend()

  plt.savefig("out/scatter_balls_vs_time.png")

  plt.close()

if __name__ == "__main__":
  main()