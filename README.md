# declone
declone commandline tool, in Go, soon Python3, Java (2020), Clojure, Swift

I want to write a tool that can examine a bunch of paths and find maximal identical subtrees.   The reason that need arises is as follows.

I have like 33 years of files accrued since high school floppies, across undergrad and grad homework, mad scientist stuff,
and they migrated over time from PC-AT to NeXT to Sun to Apples, and then the backups of those machines ended up
cloning earlier backups in subdirectories.   The result has structure something like Russian nesting dolls, with some levels holding multiple recursive dolls.

So, I spent a tedious bunch of time a few months ago reconciling over a dozen external USB hard drives, of these backups, with duplicates among them,
into one very large filesystem.

I'd like to identify cloned files and identical subtrees (i.e., folders) within such a large filesystem.
