# Differential Cryptanalysis

This is an implementation of differential cryptanalysis performed on the following substitution-permutation network :

![](https://github.com/arnaudlheureux/DifferentialCryptanalysis/blob/master/SPN.png)

A popular tutorial on differential cryptanalysis is [this one](https://www.engr.mun.ca/~howard/PAPERS/ldc_tutorial.pdf). This implementation can be used to follow along this tutorial. An assignment can be created from this tutorial and implementation by providing students with a SPN with different substitution boxes. The students all have different team numbers and their plaintexts are encrypted by a server using a different master key for every team number.

By following the tutorial and filling in the code template, one can find 8 out of 20 bits from the master key and can then use brute force to find the 12 others. The key schedule used in this implementation is entirely made-up and "similar" to the one used in DES.

