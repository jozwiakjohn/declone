// john jozwiak on 2020 June 18 (sunday) to practice in hopes of a new good and stable job, and to clean my decades of files.

package main

import "testing"

func TestPathDigestForFile(t *testing.T) {

	path := "./pathDigest_test.go"
	d := calculateFileDigest(path)
	if len(d) == 0 {
		t.Errorf("caclulateFileDigest failed")
	}
}

func TestPathDigestForFolder(t *testing.T) {

}

