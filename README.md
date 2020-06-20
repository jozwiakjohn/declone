# declone

declone is a commandline tool which I built because I needed it...and wanted to compare languages I've used in 2019 and 2020.

It has its initial implementation in Go, with soon-to-follow functionally matched 
implementations in Python3, Java9, and perhaps Clojure, Racket, and Swift.

Usage:

&nbsp;&nbsp;&nbsp; declone pathA pathB...

searches the paths, including filesystem subtrees rooted at and within them, for maximal identical subtrees.
Identical individual files will be identified, as will, recursively, identical subtrees.

