import numpy as np 
import sys
import sklearn as sk
import pandas as pd
from scipy.fftpack import fft
from scipy import spatial
from sklearn import preprocessing

# https://machinelearningmastery.com/feature-selection-machine-learning-python/

# N = 600
# # sample spacing
# T = 1.0 / 800.0
# x = np.linspace(0.0, N*T, N)
# y = np.sin(50.0 * 2.0*np.pi*x) + 0.5*np.sin(80.0 * 2.0*np.pi*x)
# yf = scipy.fftpack.fft(y)
# xf = np.linspace(0.0, 1.0/(2.0*T), N/2)





dfTrain = pd.read_csv('S001R14_train.csv', sep=',',header=None)

dfTest = pd.read_csv('S001R14_test.csv', sep=',',header=None)


# print (dfTrain.describe())

x = dfTrain.values #returns a numpy array
min_max_scaler = preprocessing.MinMaxScaler()
x_scaled = min_max_scaler.fit_transform(x)
normTrain = pd.DataFrame(x_scaled)

x = dfTest.values #returns a numpy array
min_max_scaler = preprocessing.MinMaxScaler()
x_scaled = min_max_scaler.fit_transform(x)
normTest = pd.DataFrame(x_scaled)

print (normTest.shape)



# Cosine Similarity
cosSimilarity =[]
for index, row in normTrain.iterrows():

	# fftTrain = np.fft.fft(row)
	# fftTest = np.fft.fft(dfTest.iloc[index])
	# result = 1-spatial.distance.cosine(row, dfTest.iloc[index])

	result = np.dot(row,normTest.iloc[index])
	cosSimilarity.append(result)

# print (max(cosSimilarity),min(cosSimilarity))	

normalized = (cosSimilarity-min(cosSimilarity))/(max(cosSimilarity)-min(cosSimilarity))


print (normalized)


	





