// john jozwiak on 2020 June 19 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.

package main

import (
	"path/filepath"
	"testing"
)

func TestNormalizePath(t *testing.T) {  //  this function exists to verify i use stdlib filepath.Clean properly
	problemsAndAnswers := map[string]string{
		"a//b": "a/b",
		".":    ".",
		"a/..": ".",
	}
	for in, out := range problemsAndAnswers {
		n := filepath.Clean(in)
		if n != out {
			t.Errorf("mismatch in normalizing %s -> normalized = %s which is not expected %s", in, n, out)
		}
	}
}
