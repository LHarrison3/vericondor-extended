import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit

x = np.array([2,3,4,5,6,7,8,9,10])

y_112_bal_creation = [34.805, 94.800, 172.400, 283.112, 438.060, 586.932, 755.794, 1052.266, 1190.696]
y_112_bal_creation_err = [3.675, 12.122, 68.927, 154.712, 90.490, 122.739, 68.112, 129.793, 153.040]

y_112_nizkp_verify = [48.364, 121.726, 218.537, 347.472, 515.478, 727.290, 978.741, 1269.744, 1595.415]
y_112_nizkp_verify_err = [10.355, 43.268, 8.568, 20.009, 38.056, 25.124, 183.348, 119.758, 145.825]

y_112_tally_verify = [5.562, 17.102, 35.603, 56.980, 80.582, 112.158, 158.865, 217.903, 272.097]
y_112_tally_verify_err = [0.738, 3.540, 7.533, 13.985, 23.968, 37.279, 31.491, 37.060, 24.327]

y_128_bal_creation = [82.518, 216.939, 412.293, 667.239, 987.656, 1419.245, 1857.380, 2458.044, 3009.198]
y_128_bal_creation_err = [6.801, 15.489, 46.446, 17.612, 69.252, 280.814, 320.555, 46.623, 272.192]

y_128_nizkp_verify = [112.035, 282.918, 538.886, 854.237, 1250.729, 1722.642, 2266.791, 2930.456, 3585.523]
y_128_nizkp_verify_err = [16.834, 30.413, 107.505, 140.403, 89.642, 63.513, 262.980, 398.702, 25.703]

y_128_tally_verify = [12.936, 50.814, 75.852, 117.893, 186.053, 257.401, 342.557, 489.115, 551.043]
y_128_tally_verify_err = [3.084, 8.581, 12.104, 26.208, 54.840, 57.019, 58.311, 47.992, 66.113]

def objective(x, a, b, c):
    return a*(x**2) + b*x + c

popt, _ = curve_fit(objective, x, y_112_bal_creation)
a, b, c = popt
y_112_bal_creation_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_112_nizkp_verify)
a, b, c = popt
y_112_nizkp_verify_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_112_tally_verify)
a, b, c = popt
y_112_tally_verify_new = np.array([objective(i, a, b, c) for i in x])

line_112_bal_creation = plt.errorbar(x, y_112_bal_creation_new, y_112_bal_creation_err, linewidth=1, capsize=1, linestyle='solid')
line_112_bal_creation[-1][0].set_linestyle('solid')
line_112_nizkp_verify = plt.errorbar(x, y_112_nizkp_verify_new, y_112_nizkp_verify_err, linewidth=1, capsize=1, linestyle='dashed')
line_112_nizkp_verify[-1][0].set_linestyle('dashed')
line_112_tally_verify = plt.errorbar(x, y_112_tally_verify, y_112_tally_verify_err, linewidth=1, capsize=1, linestyle='dashdot')
line_112_tally_verify[-1][0].set_linestyle('dashdot')
line_112_bal_creation.set_label("Ballot Creation")
line_112_nizkp_verify.set_label("NIZKP Verification")
line_112_tally_verify.set_label("Tally Verification")
plt.legend()
plt.xlabel('Number of Candidates')
plt.ylabel('Benchmark Time (milliseconds)')
plt.show()

popt, _ = curve_fit(objective, x, y_128_bal_creation)
a, b, c = popt
y_128_bal_creation_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_128_nizkp_verify)
a, b, c = popt
y_128_nizkp_verify_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_128_tally_verify)
a, b, c = popt
y_128_tally_verify_new = np.array([objective(i, a, b, c) for i in x])

line_128_bal_creation = plt.errorbar(x, y_128_bal_creation_new, y_128_bal_creation_err, linewidth=1, capsize=1, linestyle='solid')
line_128_bal_creation[-1][0].set_linestyle('solid')
line_128_nizkp_verify = plt.errorbar(x, y_128_nizkp_verify_new, y_128_nizkp_verify_err, linewidth=1, capsize=1, linestyle='dashed')
line_128_nizkp_verify[-1][0].set_linestyle('dashed')
line_128_tally_verify = plt.errorbar(x, y_128_tally_verify, y_128_tally_verify_err, linewidth=1, capsize=1, linestyle='dashdot')
line_128_tally_verify[-1][0].set_linestyle('dashdot')
line_128_bal_creation.set_label("Ballot Creation")
line_128_nizkp_verify.set_label("NIZKP Verification")
line_128_tally_verify.set_label("Tally Verification")
plt.legend()
plt.xlabel('Number of Candidates')
plt.ylabel('Benchmark Time (milliseconds)')
plt.show()
