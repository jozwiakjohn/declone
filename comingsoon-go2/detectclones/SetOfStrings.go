// john jozwiak on 2020 June 14 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.

package main

import "fmt"

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

func (set SetOfString) Cardinality() int {
	return len(set)
}

func (set SetOfString) ToSlice() []string {
	result := make([]string, 0)
	for s := range set {
		result = append(result, s)
	}
	return result
}

func (set SetOfString) ToString(style string, label string) string {
	items := set.ToSlice()
	r := ""
	switch style {
	case "scheme", "lisp", "racket", "t":
		r += "("
		for _, i := range items {
			r += fmt.Sprintf("\"%s\"  ", i)
		}
		r += ")"
	case "go", "golang":
		r = fmt.Sprintf("%#v", items)
	case "verbose", "english", "human":
		r += label
		for _, i := range items {
			r += fmt.Sprintf("   \"%s\"\n", i)
		}
		r += "\n"
	default:
		r = fmt.Sprintf("%#v", items)
	}
	return r
}

/*
func (set * SetOfString) union(set2 SetOfString) * SetOfString {

}

func (set * SetOfString) intersect(set2 SetOfString) * SetOfString {

}

func filterStrings(ss []string, criterion func(s string) bool) []string {
	var filtration = make([]string, 0) //  not sure how many will "survive" filtration.
	for _, s := range ss {
		if criterion(s) {
			filtration = append(filtration, s)
		}
	}
	return filtration
}

func mapStrings(ss []string, f func(s string) string) []string { // yes the map is string -> string for now.
	rslt := make([]string, len(ss))
	for i, s := range ss {
		rslt[i] = f(s)
	}
	return rslt
}
*/
