# declone  (2020 June 15 or so)

declone is a commandline tool which I built because I needed it...and wanted to compare languages I've used in 2019 and 2020.

It has its initial implementation in Go v1.14.3, followed by Python3, and coming soon functionally-matched 
implementations in Swift5 and Java9, and perhaps Clojure and Racket, depending on multi-day caffeination.

Usage:

&nbsp;&nbsp;&nbsp; declone pathA pathB...

searches the paths, including filesystem subtrees rooted at and within them, for maximal identical subtrees.
Identical individual files will be identified, as will, recursively, identical subtrees.

Eventually...it would be nice if i were to add a -photos flag, such that .jpg files could be compared by more than just contents,
or sha256 hash thereof, in hopes of associating thumbnails with originals.
