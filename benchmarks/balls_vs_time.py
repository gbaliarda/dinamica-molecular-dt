import toml
import json
import subprocess
import numpy as np
import matplotlib.pyplot as plt
import math

def main() -> None:
  # Load config
  with open("config.toml", "r") as f:
    config = toml.load(f)

  initialPositions = config["benchmarks"]["initialPositions"]
  rounds = config["benchmarks"]["rounds"]
  whiteBallYRange = config["benchmarks"]["whiteBallYRange"]
  step = (whiteBallYRange[1] - whiteBallYRange[0]) / (initialPositions - 1)

  TOTAL_BALLS = 16
  TOTAL_HOLES = 6

  # Avg times when the ball count is 0, 1, 2, ..., 16
  times = {}
  # Errors (std) for the previous times
  errors = {}
  
  for i in range(initialPositions):
    # Update white ball `y` coordinate
    current_y = whiteBallYRange[0] + step * i
    config["simulation"]["whiteBallCoords"] = [config["simulation"]["whiteBallCoords"][0], current_y]

    print("Running simulation with white ball at [", config["simulation"]["whiteBallCoords"][0], ", ", current_y, "]")

    with open("config.toml", "w") as f:
      toml.dump(config, f)

    # Save the times of each round for the current `y`
    times[current_y] = {}
    errors[current_y] = {}

    for j in range(rounds):    
      # Create particles
      subprocess.run(["python", "generate_particles.py"])

      # Run simulation
      subprocess.run(["java", "-jar", "./target/dinamica-molecular-dt-1.0-SNAPSHOT-jar-with-dependencies.jar"])

      # Save event times
      with open(config["files"]["output"], 'r') as file:
        lines = file.readlines()

      current_balls = TOTAL_BALLS
      ball_amt_curr_time = current_balls + TOTAL_HOLES

      # Initialize an array to contain the times (for all rounds) for each ball amount
      if j == 0: times[current_y][TOTAL_BALLS] = []
      times[current_y][TOTAL_BALLS].append(0)
      
      for line in lines:
        data = line.split()

        if len(data) == 1:
          # Discard the holes counted as balls
          ball_amt_curr_time -= TOTAL_HOLES

          # If the number of balls has decreased, save the time
          if ball_amt_curr_time < current_balls:
            try:
              times[current_y][ball_amt_curr_time].append(round(time, 3))
            except KeyError:
              times[current_y][ball_amt_curr_time] = [round(time, 3)] # initialize the array of times for the current ball amount

            current_balls = ball_amt_curr_time # should be 1 less ball

          time = float(data[0])
          ball_amt_curr_time = 0
        else:
          ball_amt_curr_time += 1
      
      if j == 0: times[current_y][0] = []
      times[current_y][0].append(round(time, 3))

  print("\nRAW VALUES")
  print(times)
  print(errors)

  # Take the mean and std of the times for each ball amount
  for current_y in times.keys():
    for ball_amt in times[current_y].keys():
      errors[current_y][ball_amt] = round(np.std(times[current_y][ball_amt]), 3)

      if (ball_amt == 0): # use standard error for ball_amt = 0 instead of std
        errors[current_y][ball_amt] /= math.sqrt(len(times[current_y][ball_amt]))

      times[current_y][ball_amt] = round(np.mean(times[current_y][ball_amt]), 3)

  # Save the mean and error values
  with open('out/times.json', 'w') as file:
    json.dump(times, file)
  with open('out/errors.json', 'w') as file:
    json.dump(errors, file)

  print("\nMEAN VALUES")
  print(times)
  print(errors)

  # Plot mean time until half of the balls are gone
  plot_half_balls_vs_time(times, errors)

  # Plot mean time until all the balls are gone
  plot_zero_balls_vs_time(times, errors)


def plot_half_balls_vs_time(times: dict[float, dict[int, float]], errors: dict[float, dict[int, float]]):
  x_values = []
  y_values = []
  stds = []

  # Time until half of the balls are gone
  for y in times.keys():
    x_values.append(y)
    print("HALF BALLS AT:", y)
    y_values.append(times[y][8])
    stds.append(errors[y][8])

  _, ax = plt.subplots(figsize=(16, 6))

  ax.bar(x_values, y_values, yerr=stds, capsize=5, width=0.5)

  ax.set_xlabel("Coordenada `y` bola blanca (cm)", fontsize=18)
  ax.set_ylabel("Tiempo hasta mitad de bolas (s)", fontsize=18)

  ax.grid()
  plt.tight_layout()
  
  # plt.show()
  plt.savefig("out/half_balls_times.png")

  plt.close()


def plot_zero_balls_vs_time(times: dict[float, dict[int, float]], errors: dict[float, dict[int, float]]):
  x_values = []
  y_values = []
  stds = []

  # Time until all the balls are gone
  for y in times.keys():
    print("ZERO BALLS AT:", y)
    x_values.append(y)
    y_values.append(times[y][0])
    stds.append(errors[y][0])
  
  _, ax = plt.subplots(figsize=(16, 6))

  ax.bar(x_values, y_values, yerr=stds, capsize=5, width=0.5)

  ax.set_xlabel("Coordenada `y` bola blanca (cm)", fontsize=18)
  ax.set_ylabel("Tiempo hasta finalizar (s)", fontsize=18)

  ax.grid()
  plt.tight_layout()
  
  # plt.show()
  plt.savefig("out/zero_balls_times.png")

  plt.close()


if __name__ == "__main__":
  main()
