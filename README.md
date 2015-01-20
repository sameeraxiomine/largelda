# Largelda #

An implementation of Topic Modeling using Latent Dirichlet Allocation algorithm which uses Memory Mapped Files to 
overcome the Heap Memory Limitations. This implementation will allow you to scale LDA to millions of documents on a single
machine. 

Currently updating this Library to use the LargeCollections library I have developed. The Wiki is still work in progress. 
Please send me an email to sameer@axiomne.com if you need any support to run this library. I will be happy to help out 
with respect to user documentation.

# User Instructions #

To create the model use the following command
execwindows.cmd createModel

To apply the model generated in the last step execute the following command
execwindows.cmd useModel

##Configuration##
The configurations are provided in the config/TopicModeling.properties file

##Sample Training Data##
The sample training data is in the data/training/ap.txt file
