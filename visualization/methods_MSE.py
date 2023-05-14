import matplotlib.pyplot as plt

def main() -> None:
    t = [10**-2, 10**-3, 10**-4, 10**-5, 10**-6]
    mse_verlet = [3.8063373104162205E-6, 3.802079276766962E-10, 3.793617183608239E-14, 1.726866010017299E-16, 9.00135605744398E-14]
    mse_beeman = [3.4796913005274814E-6, 3.314634679533107E-10, 3.2977367780957026E-14, 3.2793289464863702E-18, 6.780522234693497E-21]
    mse_gear = [4.0487214091967824E-12, 2.9992922517954894E-22, 1.4362728316627928E-24, 5.572282110197612E-23, 6.25488030892065E-21]

    plt.loglog(t, mse_verlet, linestyle='-', marker='o', label='Verlet')
    plt.loglog(t, mse_beeman, linestyle='-', marker='o', label='Beeman')
    plt.loglog(t, mse_gear, linestyle='-', marker='o', label='Gear Corrector Predictor')

    plt.xlabel("Tiempo (s)", fontsize=20)
    plt.ylabel("MSE", fontsize=20)
    
    plt.legend()
    plt.tight_layout()
    plt.show()


if __name__ == '__main__':
    main()