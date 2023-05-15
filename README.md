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
exercise = 2
dtSimulation = 0.001 # >= 10^-3, otherwise it won't work correctly
outputInterval = 10
verbose = false
tableWidth = 224
tableHeight = 112
whiteBallCoords = [ 56.0, 56.0,]
whiteBallVx = 100.0
ballMass = 165
ballDiameter = 5.7
integration = "gear"
generateFixed = true # generate holes in the table

[files]
staticInput = "./static.txt"
output = "./out/output.txt"

[benchmarks]
whiteBallYRange = [ 42.0, 56.0,]
initialPositions = 20
rounds = 5
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
time_0_method_1
x0 v0
time_1_method_1
x1 v1
time_2_method_1
...
time_0_method_2
x0 v0
time_1_method_2
x1 v1
time_2_method_2
...
time_0_method_3
x0 v0
time_1_method_3
x1 v1
time_2_method_3
...
```

For **exercise 2** (Billard Table):

```
time_0
particle_1_x particle_1_y particle_1_vx particle_1_vy particle_1_fx particle_1_fy particle_1_radius particle_1_color
particle_2_x particle_2_y particle_2_vx particle_2_vy particle_1_fx particle_1_fy particle_2_radius particle_2_color
...
particle_N_x particle_N_y particle_N_vx particle_N_vy particle_1_fx particle_1_fy particle_N_radius particle_N_color
time_1
...
```

# Run Animation

To run the animations based on the simulation output, execute from the root folder:

```shell
python visualization/visuals_<exercise>.py
```

# Run benchmarks

To run benchmarks on the amount of balls over time, execute from the root folder:

```shell
python benchmarks/<benchmark_file>.py
```

# Authors

- Baliarda Gonzalo - 61490
- Pérez Ezequiel - 61475