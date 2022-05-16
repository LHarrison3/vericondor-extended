import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit

x = np.array([2,3,4,5,6,7,8,9,10])

# base

'''
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
'''

# preference relations

'''
y_112_bal_creation_pref = [11.643, 32.615, 58.608, 98.389, 163.354, 225.123, 316.624, 387.522, 425.076]
y_112_bal_creation_pref_err = [2.591, 7.004, 1.845, 6.993, 8.691, 10.301, 23.571, 17.511, 13.082]

y_112_nizkp_verify_pref = [25.679, 63.950, 121.466, 218.683, 302.599, 385.661, 506.750, 647.514, 989.688]
y_112_nizkp_verify_pref_err = [2.969, 2.475, 4.526, 18.696, 33.194, 1.015, 5.440, 13.034, 179.486]

y_112_tally_verify_pref = [2.861, 9.560, 19.115, 30.583, 46.019, 71.564, 82.189, 111.417, 119.139]
y_112_tally_verify_pref_err = [3.997, 4.250, 3.169, 6.526, 4.950, 103.791, 51.469, 50.492, 11.380]

y_128_bal_creation_pref = [25.499, 69.818, 133.965, 219.081, 371.556, 528.259, 681.381, 809.452, 1016.446]
y_128_bal_creation_pref_err = [1.445, 6.666, 5.139, 8.532, 20.358, 40.068, 20.531, 29.585, 61.491]

y_128_nizkp_verify_pref = [60.159, 162.508, 313.273, 527.085, 765.652, 988.855, 1316.067, 1939.607, 2310.894]
y_128_nizkp_verify_pref_err = [2.100, 10.242, 25.116, 32.581, 61.013, 13.945, 46.717, 229.500, 390.956]

y_128_tally_verify_pref = [6.533, 20.321, 38.248, 66.511, 96.258, 136.823, 175.436, 230.489, 291.326]
y_128_tally_verify_pref_err = [14.563, 35.670, 29.666, 16.998, 12.885, 26.730, 36.004, 128.202, 120.270]
'''

# indifference

y_112_bal_creation_indiff = [47.738, 128.141, 274.817, 454.813, 687.820, 870.548, 1198.178, 1396.379, 1896.683]
y_112_bal_creation_indiff_err = [15.136, 44.664, 47.265, 59.608, 88.952, 183.229, 89.626, 175.680, 422.942]

y_112_nizkp_verify_indiff = [96.654, 317.583, 615.782, 1161.862, 1536.599, 2249.889, 3033.514, 3536.691, 4222.178]
y_112_nizkp_verify_indiff_err = [53.701, 139.525, 212.379, 191.650, 579.279, 755.983, 1192.506, 635.903, 1726.347]

y_112_tally_verify_indiff = [5.462, 16.766, 32.328, 55.100, 88.399, 125.404, 168.046, 196.188, 243.600]
y_112_tally_verify_indiff_err = [1.237, 8.466, 5.562, 26.459, 81.220, 39.307, 71.899, 202.530, 162.758]

y_128_bal_creation_indiff = [85.849, 253.047, 489.715, 775.765, 1217.723, 1483.206, 2056.803, 2685.828, 3894.009]
y_128_bal_creation_indiff_err = [38.661, 121.830, 84.344, 128.813, 328.561, 496.840, 419.714, 619.381, 419.012]

y_128_nizkp_verify_indiff = [247.394, 724.674, 1489.205, 2387.576, 2824.937, 4041.124, 5596.911, 7102.411, 9834.354]
y_128_nizkp_verify_indiff_err = [101.382, 273.314, 585.340, 549.663, 575.337, 422.325, 1058.333, 931.887, 2884.577]

y_128_tally_verify_indiff = [13.131, 34.105, 66.999, 112.577, 169.497, 275.520, 369.719, 466.705, 537.442]
y_128_tally_verify_indiff_err = [60.532, 35.705, 23.021, 19.115, 18.341, 47.344, 135.284, 25.327, 104.694]



def objective(x, a, b, c):
    return a*(x**2) + b*x + c

# base

'''
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
'''

# preference relations

'''
popt, _ = curve_fit(objective, x, y_112_bal_creation_pref)
a, b, c = popt
y_112_bal_creation_pref_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_112_nizkp_verify_pref)
a, b, c = popt
y_112_nizkp_verify_pref_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_112_tally_verify_pref)
a, b, c = popt
y_112_tally_verify_pref_new = np.array([objective(i, a, b, c) for i in x])

line_112_bal_creation = plt.errorbar(x, y_112_bal_creation_pref_new, y_112_bal_creation_pref_err, linewidth=1, capsize=1, linestyle='solid')
line_112_bal_creation[-1][0].set_linestyle('solid')
line_112_nizkp_verify = plt.errorbar(x, y_112_nizkp_verify_pref_new, y_112_nizkp_verify_pref_err, linewidth=1, capsize=1, linestyle='dashed')
line_112_nizkp_verify[-1][0].set_linestyle('dashed')
line_112_tally_verify = plt.errorbar(x, y_112_tally_verify_pref_new, y_112_tally_verify_pref_err, linewidth=1, capsize=1, linestyle='dashdot')
line_112_tally_verify[-1][0].set_linestyle('dashdot')
line_112_bal_creation.set_label("Ballot Creation")
line_112_nizkp_verify.set_label("NIZKP Verification")
line_112_tally_verify.set_label("Tally Verification")
plt.legend()
plt.xlabel('Number of Candidates')
plt.ylabel('Benchmark Time (milliseconds)')
plt.show()
'''

# indifference

popt, _ = curve_fit(objective, x, y_112_bal_creation_indiff)
a, b, c = popt
y_112_bal_creation_indiff_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_112_nizkp_verify_indiff)
a, b, c = popt
y_112_nizkp_verify_indiff_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_112_tally_verify_indiff)
a, b, c = popt
y_112_tally_verify_indiff_new = np.array([objective(i, a, b, c) for i in x])

line_112_bal_creation = plt.errorbar(x, y_112_bal_creation_indiff_new, y_112_bal_creation_indiff_err, linewidth=1, capsize=1, linestyle='solid')
line_112_bal_creation[-1][0].set_linestyle('solid')
line_112_nizkp_verify = plt.errorbar(x, y_112_nizkp_verify_indiff_new, y_112_nizkp_verify_indiff_err, linewidth=1, capsize=1, linestyle='dashed')
line_112_nizkp_verify[-1][0].set_linestyle('dashed')
line_112_tally_verify = plt.errorbar(x, y_112_tally_verify_indiff_new, y_112_tally_verify_indiff_err, linewidth=1, capsize=1, linestyle='dashdot')
line_112_tally_verify[-1][0].set_linestyle('dashdot')
line_112_bal_creation.set_label("Ballot Creation")
line_112_nizkp_verify.set_label("NIZKP Verification")
line_112_tally_verify.set_label("Tally Verification")
plt.legend()
plt.xlabel('Number of Candidates')
plt.ylabel('Benchmark Time (milliseconds)')
plt.show()

# base

'''
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
'''

# preference relations

'''
popt, _ = curve_fit(objective, x, y_128_bal_creation_pref)
a, b, c = popt
y_128_bal_creation_pref_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_128_nizkp_verify_pref)
a, b, c = popt
y_128_nizkp_verify_pref_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_128_tally_verify_pref)
a, b, c = popt
y_128_tally_verify_pref_new = np.array([objective(i, a, b, c) for i in x])

line_128_bal_creation = plt.errorbar(x, y_128_bal_creation_pref_new, y_128_bal_creation_pref_err, linewidth=1, capsize=1, linestyle='solid')
line_128_bal_creation[-1][0].set_linestyle('solid')
line_128_nizkp_verify = plt.errorbar(x, y_128_nizkp_verify_pref_new, y_128_nizkp_verify_pref_err, linewidth=1, capsize=1, linestyle='dashed')
line_128_nizkp_verify[-1][0].set_linestyle('dashed')
line_128_tally_verify = plt.errorbar(x, y_128_tally_verify_pref_new, y_128_tally_verify_pref_err, linewidth=1, capsize=1, linestyle='dashdot')
line_128_tally_verify[-1][0].set_linestyle('dashdot')
line_128_bal_creation.set_label("Ballot Creation")
line_128_nizkp_verify.set_label("NIZKP Verification")
line_128_tally_verify.set_label("Tally Verification")
plt.legend()
plt.xlabel('Number of Candidates')
plt.ylabel('Benchmark Time (milliseconds)')
plt.show()
'''

# indifference

popt, _ = curve_fit(objective, x, y_128_bal_creation_indiff)
a, b, c = popt
y_128_bal_creation_indiff_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_128_nizkp_verify_indiff)
a, b, c = popt
y_128_nizkp_verify_indiff_new = np.array([objective(i, a, b, c) for i in x])

popt, _ = curve_fit(objective, x, y_128_tally_verify_indiff)
a, b, c = popt
y_128_tally_verify_indiff_new = np.array([objective(i, a, b, c) for i in x])

line_128_bal_creation = plt.errorbar(x, y_128_bal_creation_indiff_new, y_128_bal_creation_indiff_err, linewidth=1, capsize=1, linestyle='solid')
line_128_bal_creation[-1][0].set_linestyle('solid')
line_128_nizkp_verify = plt.errorbar(x, y_128_nizkp_verify_indiff_new, y_128_nizkp_verify_indiff_err, linewidth=1, capsize=1, linestyle='dashed')
line_128_nizkp_verify[-1][0].set_linestyle('dashed')
line_128_tally_verify = plt.errorbar(x, y_128_tally_verify_indiff_new, y_128_tally_verify_indiff_err, linewidth=1, capsize=1, linestyle='dashdot')
line_128_tally_verify[-1][0].set_linestyle('dashdot')
line_128_bal_creation.set_label("Ballot Creation")
line_128_nizkp_verify.set_label("NIZKP Verification")
line_128_tally_verify.set_label("Tally Verification")
plt.legend()
plt.xlabel('Number of Candidates')
plt.ylabel('Benchmark Time (milliseconds)')
plt.show()
