import matplotlib.pyplot as plt
import numpy as np

with open('./out/k_difference.txt', 'r') as f3:
    lineas3 = f3.readlines()
with open('./out/k_difference.txt', 'r') as f4:
    lineas4 = f4.readlines()

t3 = []
v3 = []
t4 = []
v4 = []

for i in range(0, len(lineas3), 2):
    tiempo = float(lineas3[i].strip())
    valor = float(lineas3[i+1].strip())
    t3.append(tiempo)
    v3.append(valor)

for i in range(0, len(lineas4), 2):
    tiempo = float(lineas4[i].strip())
    valor = float(lineas4[i+1].strip())
    t4.append(tiempo)
    v4.append(valor)

x3 = np.array(t3)
y3 = np.array(v3)
x4 = np.array(t4)
y4 = np.array(v4)

plt.semilogy(x3, y3, label="K=3")
plt.semilogy(x4, y4, label="K=4")
plt.xlabel('Tiempo')
plt.ylabel('Valor')
plt.legend()
plt.show()
