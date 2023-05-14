import numpy as np
import matplotlib.pyplot as plt

def main() -> None:
    t = [10**-2, 10**-3, 10**-4, 10**-5, 10**-6]
    et_verlet_k2 = [0.000742799, 0.000127000, 0.000104100, 9.36010e-05, 7.34010e-05, 0.000151101, 0.000142400, 9.46990e-05, 8.87010e-05, 7.51000e-05]
    et_verlet_k3 = [0.00465290, 0.000931700, 0.000819600, 0.000889700, 0.00118380, 0.000700799, 0.00167640, 0.000852801, 0.000739300, 0.00184770]
    et_verlet_k4 = [0.0219359, 0.0118879, 0.0072288, 0.004808, 0.0062891, 0.004491, 0.0039289, 0.0039733, 0.003639, 0.0034817]
    et_verlet_k5 = [0.0491842, 0.0387368, 0.0337180, 0.0550820, 0.0454405, 0.0340692, 0.0348624, 0.0310696, 0.0360951, 0.0330255]
    et_verlet_k6 = [0.402234, 0.321892, 0.306707, 0.333627, 0.342386, 0.377039, 0.334922, 0.335149, 0.312708, 0.324188]
    et_beeman_k2 = [0.000679100, 0.000147300, 0.000110100, 0.000317000, 0.000140700, 0.000174199, 9.24000e-05, 7.38000e-05, 0.000106100, 7.82000e-05]
    et_beeman_k3 = [0.00499820, 0.00307970, 0.000869401, 0.000839399, 0.00121410, 0.000866900, 0.00129690, 0.00111500, 0.000766000, 0.00174810]
    et_beeman_k4 = [0.0104400, 0.0117281, 0.00629790, 0.00616250, 0.00590010, 0.00465040, 0.00385110, 0.00545960, 0.00347720, 0.00364890]
    et_beeman_k5 = [0.0537900, 0.0550086, 0.0402222, 0.103156, 0.0559186, 0.0400981, 0.0460646, 0.0341914, 0.0402295, 0.0337937]
    et_beeman_k6 = [0.387944, 0.394333, 0.346760, 0.352578, 0.341952, 0.335187, 0.336434, 0.352441, 0.331795, 0.391912]
    et_gear_k2 = [0.00133280, 0.000341999, 0.000406499, 0.000505500, 0.000587899, 0.000470501, 0.000322399, 0.000282000, 0.000290100, 0.000279700]
    et_gear_k3 = [0.00498870, 0.00432060, 0.00398220, 0.00375070, 0.00300580, 0.00242900, 0.00425860, 0.00265560, 0.00275130, 0.00476910]
    et_gear_k4 = [0.0513957, 0.0341555, 0.0327675, 0.0323120, 0.0340667, 0.0356700, 0.0291405, 0.0262866, 0.0200755, 0.0184693]
    et_gear_k5 = [0.213819, 0.199551, 0.249323, 0.224113, 0.245115, 0.208129, 0.198274, 0.180000, 0.199725, 0.194988]
    et_gear_k6 = [2.21805, 1.77101, 1.89011, 1.72447, 1.83102, 1.81887, 1.89254, 1.83474, 1.75202, 1.80794]
    mean_execute_time_verlet = [np.mean(et_verlet_k2), np.mean(et_verlet_k3), np.mean(et_verlet_k4), np.mean(et_verlet_k5), np.mean(et_verlet_k6)]
    mean_execute_time_beeman = [np.mean(et_beeman_k2), np.mean(et_beeman_k3), np.mean(et_beeman_k4), np.mean(et_beeman_k5), np.mean(et_beeman_k6)]
    mean_execute_time_gear = [np.mean(et_gear_k2), np.mean(et_gear_k3), np.mean(et_gear_k4), np.mean(et_gear_k5), np.mean(et_gear_k6)]
    std_execute_time_verlet = [np.std(et_verlet_k2), np.std(et_verlet_k3), np.std(et_verlet_k4), np.std(et_verlet_k5), np.std(et_verlet_k6)]
    std_execute_time_beeman = [np.std(et_beeman_k2), np.std(et_beeman_k3), np.std(et_beeman_k4), np.std(et_beeman_k5), np.std(et_beeman_k6)]
    std_execute_time_gear = [np.std(et_gear_k2), np.std(et_gear_k3), np.std(et_gear_k4), np.std(et_gear_k5), np.std(et_gear_k6)]

    plt.errorbar(t, mean_execute_time_verlet, yerr=std_execute_time_verlet, fmt="o", linewidth=1, markersize=0, capsize=3, color='black')
    plt.errorbar(t, mean_execute_time_beeman, yerr=std_execute_time_beeman, fmt="o", linewidth=1, markersize=0, capsize=3, color='black')
    plt.errorbar(t, mean_execute_time_gear, yerr=std_execute_time_gear, fmt="o", linewidth=1, markersize=0, capsize=3, color='black')

    plt.semilogx(t, mean_execute_time_verlet, linestyle='-', marker='o', markersize=4, linewidth=1, label='verlet', color='blue')
    plt.semilogx(t, mean_execute_time_beeman, linestyle='-', marker='o', markersize=4, linewidth=1, label='beeman', color='green')
    plt.semilogx(t, mean_execute_time_gear, linestyle='-', marker='o', markersize=4, linewidth=1, label='gear', color='red')
    plt.xlabel("Paso Temporal (s)", fontsize=20)
    plt.ylabel("Tiempo de ejecución (s)", fontsize=20)
    plt.legend()

    plt.tight_layout()
    plt.show()


if __name__ == '__main__':
    main()