fileIndex = num2str( 2);

arMagnitude = importdata(strcat('C:\Users\Nati\Desktop\Files_downloaded_by_AirDroid (1)\m_ftMagnitude',fileIndex));
arIn = importdata(strcat('C:\Users\Nati\Desktop\Files_downloaded_by_AirDroid (1)\m_in1', fileIndex));
arAndHPS = importdata(strcat('C:\Users\Nati\Desktop\Files_downloaded_by_AirDroid (1)\arhps', fileIndex));
%plotMagnitude = semilogx(arMagnitude);
Fs = 44100;
fftSize = 32768;
frameSize = 16384;
%set(gca, 'XTick', 0:10:200);
RealFFT = abs(fft(arIn,fftSize));
RealFFT = RealFFT(1 : fftSize / 2 + 1);
f = Fs/2*linspace(0,1,fftSize/2 + 1);
figure(1);
semilogx(f,2*abs(RealFFT(1:fftSize / 2 + 1)));
%semilogx(arMagnitude);
figure(2);
% calc HPS
PitchSpectralHps(arMagnitude, Fs);