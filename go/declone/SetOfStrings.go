// john jozwiak on 2020 June 14 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.

package main

type SetOfString map[string]struct{}

func MakeSetOfString(ss []string) SetOfString {

	result := make(SetOfString)
	for _, s := range ss {
		result.InsertBang(s)
	}
	return result
}

func (set SetOfString) InsertBang(s string) SetOfString {
	set[s] = struct{}{} //  takes no useless byte space.
	return set
}

func (set SetOfString) RemoveBang(s string) SetOfString {
	delete(set, s)
	return set
}

func (set SetOfString) Contains(s string) bool {
	_, found := set[s]
	return found
}

func (set SetOfString) ToSlice() []string {
	result := make([]string, 0)
	for s := range set {
		result = append(result, s)
	}
	return result
}

// func (set * SetOfString) union(set2 SetOfString) * SetOfString {
//
// }

// func (set * SetOfString) intersect(set2 SetOfString) * SetOfString {
//
// }
