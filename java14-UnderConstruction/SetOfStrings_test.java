// john jozwiak on 2020 June 14 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.

package main

import (
	"testing"
)

func TestInsert(t *testing.T) {

	s := make(SetOfString)
	insertWorks := s.InsertBang("a").Contains("a")
	if !insertWorks {
		t.Errorf("insert failed, as assessed by contains")
	}
}

func TestDelete(t *testing.T) {

	s := make(SetOfString)
	s.InsertBang("a")
	insertWorked := s.Contains("a")
	s.RemoveBang("a")
	deleteWorked := !s.Contains("a")

	if !(insertWorked && deleteWorked) {
		t.Errorf("insert followed by deleted failed, as assessed by contains")
	}
}

func TestIsInsertSideEffecting(t *testing.T) {

	s := MakeSetOfString([]string{"a", "b"})
	s.InsertBang("c")

	if !s.Contains("c") {
		t.Errorf("SetOfStrings Insert does NOT side-effect, to be clear")
	}
}
