import pyedflib
import numpy as np
import sys
filename = sys.argv[1].split('.')[0]+'.csv'
f=pyedflib.EdfReader(sys.argv[1])
signal_labels = f.getSignalLabels()
n = f.signals_in_file
sigbufs = np.zeros((n, f.getNSamples()[0]))
for i in np.arange(n):
	sigbufs[i, :] = f.readSignal(i)

np.savetxt(filename,sigbufs,delimiter=",")
