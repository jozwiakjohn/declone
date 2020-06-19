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
