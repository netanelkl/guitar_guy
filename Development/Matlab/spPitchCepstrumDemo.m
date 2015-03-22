function spPitchCepstrumDemo 
 [x, fs] = wavread('C:\Users\Nati\Desktop\LowE_Cut.wav');
 x = x(10000:16000);
 % x = wgn(1, 1000, 2); fs = 16000;
 c = spCepstrum(x, fs, 'hamming', 'plot');
 f0 = spPitchCepstrum(c, fs)