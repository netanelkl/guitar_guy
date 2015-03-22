/* File : example.i */
%module chordlib

%{
#include "JNIInterface.h"
%}

%include "std_vector.i"
%include "std_string.i"
%import  "NNLSBase.h"
/* Let's just grab the original header file here */
%include "JNIInterface.h"