package main

type SetOfString map[string]struct{}

func (set SetOfString) Insert(s string) SetOfString {
	set[s] = struct{}{} //  takes no useless byte space.
	return set
}

func (set SetOfString) Remove(s string) SetOfString {
	delete(set, s)
	return set
}

func (set SetOfString) Contains(s string) bool {
	_, found := set[s]
	return found
}

// func (set * SetOfString) union(set2 SetOfString) * SetOfString {
//
// }

// func (set * SetOfString) intersect(set2 SetOfString) * SetOfString {
//
// }
