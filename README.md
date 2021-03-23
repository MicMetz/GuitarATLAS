 #############################################################################
 ###                                                                       ###
 ### Title:         Guitar Hero                                            ###
 ###                                                                       ###
 ### Files:         GuitarHero.java            	                           ###
 ### Author(s):     Michael Metz (mime9599@colorado.edu)                   ###
 ### Semester:      Spring 2021                                            ###
 ### Written:       March 16, 2021                                         ###
 ### Description:   JavaFX application that simulates plucking a guitar    ###
 ###                string using the Karplus-Strong algorithm.             ###
 ### License:                                                              ###
 ### Credits:                                                              ###
 #############################################################################
 
 
 The basic idea of this program is that when a guitar string is plucked (or a piano wire struck), the string vibrates and creates sound. The length of the string determines its fundamental frequency of vibration. We can model a guitar string by sampling its displacement (a real number between -1/2 and +1/2) at N equally spaced points (in time), where N equals the sampling rate (44,100) divided by the fundamental frequency (rounding the quotient up to the nearest integer).


Plucking the string: The excitation of the string can contain energy at any frequency. We simulate the excitation with white noise: set each of the N displacements to a random real number between -1/2 and +1/2.


The resulting vibrations: After the string is plucked, the string vibrates. The pluck causes a displacement which spreads wave-like over time. The Karplus-Strong algorithm simulates this vibration by maintaining a fixed length queue of the N samples: the algorithm repeatedly deletes the first sample from the buffer and adds to the end of the buffer the average of the first two samples, scaled by an energy decay factor of 0.994.


Why it works? The two primary components that make the Karplus-Strong algorithm work are the fixed capacity queue feedback mechanism and the averaging operation.

    The fixed capacity queue feedback mechanism: The queue models the medium (a string tied down at both ends) in which the energy travels back and forth. The length of the queue determines the fundamental frequency of the resulting sound. Sonically, the feedback mechanism reinforces only the fundamental frequency and its harmonics (frequencies at integer multiples of the fundamental). The energy decay factor (.994 in this case) models the slight dissipation in energy as the wave makes a roundtrip through the string.

    The averaging operation: The averaging operation serves as a gentle low-pass filter (which removes higher frequencies while allowing lower frequencies to pass, hence the name). Because it is in the path of the feedback, this has the effect of gradually attenuating the higher harmonics while keeping the lower ones, which corresponds closely with how a plucked guitar string sounds.

From a mathematical physics viewpoint, the Karplus-Strong algorithm approximately solves the 1D wave equation, which describes the transverse motion of the string as a function of time.
