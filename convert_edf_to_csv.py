import pyedflib
import numpy as np
import sys
import pdb
filename_test = sys.argv[1].split('.')[0]+'_test.csv'
filename_train = sys.argv[1].split('.')[0]+'_train.csv'
f=pyedflib.EdfReader(sys.argv[1])
signal_labels = f.getSignalLabels()
n = f.signals_in_file
sigbufs = np.zeros((n, f.getNSamples()[0]))
for i in np.arange(n):
	sigbufs[i, :] = f.readSignal(i)
#pdb.set_trace()
#sigbufs = np.transpose(sigbufs)
sigbufs_test = np.hsplit(sigbufs, 2)[0]
sigbufs_train = np.hsplit(sigbufs, 2)[1]
np.savetxt(filename_test,sigbufs_test,delimiter=",")
np.savetxt(filename_train,sigbufs_train,delimiter=",")
