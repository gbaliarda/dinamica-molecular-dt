# Requirements

- python >= 3.10
  - numpy
  - matplotlib
  - tomllib
- java >= 11

# Configuration

Project configuration can be changed modifying the `config.toml` file:

```toml
[simulation]
exercise = 1       # exercise to run
dtSimulation = 0.1 # s
outputInterval = 2 # time steps interval
verbose = true     # enable simulation prints
timeSteps = 50     # time intervals to run the simulation

[files]
staticInput = "./static.txt"
output = "./out/output.txt"
```

# Run Simulation

To generate the `.jar` file run the following command:

```shell  
mvn clean package
```

In order to run the simulation run:

```shell
java -jar ./target/dinamica-molecular-dt-1.0-SNAPSHOT-jar-with-dependencies.jar
```

This will generate a file `out/output.txt`, whose structure is:

For **exercise 1** (Damped Oscillator):

```
time_0
x0 v0
time_1
x1 v1
time_2
...
```

# Run Animation

To run the animations based on the simulation output, execute from the root folder:

```shell
python visualization/visuals_<exercise>.py
```

# Authors

- Baliarda Gonzalo - 61490
- Pérez Ezequiel - 61475