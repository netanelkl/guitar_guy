function [f] = PitchSpectralHps (X, f_s)
 
    % initialize
    iOrder  = 7;
    f_min   = 50;
 
    afHps   = X;
    k_min   = round(f_min/f_s * 2 * size(X,1));
    
    % compute the HPS
    for (j = 2:iOrder)
        afHps   = afHps .* [X(1:j:end,:); zeros(size(X,1)-size(X(1:j:end,:),1), size(X,2))];
    end
    afHps = afHps(k_min:end,:);
    % find max index and convert to Hz
    [fDummy,freqIndex]  = max(afHps,[],1);
    freqIndex = int64(freqIndex);
    
    % proportion between the cum mul of the suggested harmony to the
    % 'parent'.
    nParentFreqIndex = (freqIndex + k_min) / 2 - (k_min - 1);
    nLocalMaximumSearchWidth = 1;
    [temp,index] = max(afHps(nParentFreqIndex-nLocalMaximumSearchWidth:1:nParentFreqIndex + nLocalMaximumSearchWidth));
    nParentFreqIndex = nParentFreqIndex - nLocalMaximumSearchWidth + index - 1;
    diff = afHps(nParentFreqIndex) / afHps(freqIndex); 
    if(diff > 0.01)
       fprintf('cutting off.');
       freqIndex = nParentFreqIndex; 
    end
    freqIndex = double(freqIndex);
    semilogx(afHps);
    freqIndex
    freqIndex           = (f_s * (freqIndex + k_min - 1)) / (size(X,1) * 2);
    freqIndex
end