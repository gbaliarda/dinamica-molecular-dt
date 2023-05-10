import tomllib
import matplotlib.pyplot as plt

def main() -> None:
    with open("config.toml", "rb") as f:
        config = tomllib.load(f)

    positions = parse_output(config["files"]["output"])
    t = list(positions.keys())
    x = list(positions.values())

    plt.plot(t, x, linestyle='-', marker='o', markersize=2, linewidth=1)
    plt.xlabel("Tiempo (s)", fontsize=20)
    plt.ylabel("Posición (m)", fontsize=20)

    plt.tight_layout()
    plt.show()


def parse_output(outputPath: str) -> dict[float, float]:
    """
    Parse the output file and return a dictionary with
    the time as key and the positions as values.
    """
    with open(outputPath, 'r') as file:
        lines = file.readlines()

    positions = {}
    time = None

    for line in lines:
        data = line.split()

        if len(data) == 1:
            time = float(data[0])
        else:
            # velocity = float(data[1])
            positions[time] = float(data[0])
    
    return positions


if __name__ == '__main__':
    main()
