import tomllib
import math
import matplotlib.pyplot as plt
import numpy as np

def main() -> None:
    with open("config.toml", "rb") as f:
        config = tomllib.load(f)
    
    integration_methods = ["Verlet", "Beeman", "Gear Predictor Corrector"]

    positions = parse_output(config["files"]["output"], integration_methods)

    t = list(positions["Verlet"].keys()) # same for all methods

    # Plot analytical solution
    plt.plot(t, get_analytical_positions(np.array(t)), label="Analítica", linestyle='-')

    for method in integration_methods:
        x = list(positions[method].values())
        
        if method == "Gear Predictor Corrector":
            linestyle = ':'
        elif method == "Beeman":
            linestyle = '--'
        else:
            linestyle = '-.'
        
        plt.plot(t, x, label=f"{method}", linestyle=linestyle)

    plt.xlabel("Tiempo (s)", fontsize=20)
    plt.ylabel("Posición (m)", fontsize=20)

    plt.tight_layout()
    plt.legend(loc="lower right")

    plt.show()


def parse_output(outputPath: str, methods: list[str]) -> dict[str, dict[float, float]]:
    """
    Parse the output file and return a dictionary with the
    times and the positions for each integration method.
    """
    with open(outputPath, 'r') as file:
        lines = file.readlines()
  
    method_idx = -1

    positions = {}
    time = None

    for line in lines:
        data = line.split()

        if len(data) == 1:
            time = float(data[0])

            if time == 0:
                method_idx += 1
                positions[methods[method_idx]] = {}
        else:
            # velocity = float(data[1])
            positions[methods[method_idx]][time] = float(data[0])
    
    return positions


def get_analytical_positions(t: np.array) -> list[float]:
    """
    Return the analytical position for the given time.
    """
    A = 1.0       # m
    gamma = 100.0 # kg/s
    k = 10**4     # N/m
    m = 70.0      # kg
    return A * np.exp(-gamma*t/(2*m)) * np.cos(math.sqrt(k/m - gamma**2/(4*m**2))*t)


if __name__ == '__main__':
    main()
