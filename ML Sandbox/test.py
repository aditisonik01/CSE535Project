import numpy as np 
import sys
import sklearn as sk
import pandas as pd
from scipy.fftpack import fft

# https://machinelearningmastery.com/feature-selection-machine-learning-python/

# N = 600
# # sample spacing
# T = 1.0 / 800.0
# x = np.linspace(0.0, N*T, N)
# y = np.sin(50.0 * 2.0*np.pi*x) + 0.5*np.sin(80.0 * 2.0*np.pi*x)
# yf = scipy.fftpack.fft(y)
# xf = np.linspace(0.0, 1.0/(2.0*T), N/2)





df = pd.read_csv('S001R14_train.csv', sep=',',header=None)
fft = np.fft.fftn(df)
print ((fft).shape)





