// john jozwiak on 2020 June 18..20 to practice in hopes of a new good and stable job, and to clean my decades of files.

package main

import (
	"crypto/sha256"
	"fmt"
	"io"
	"io/ioutil"
	"os"
	"path/filepath"
)

func calculateFileDigest(path string) string {

	// p names a file, so calculate a sha256 hash of it to store with its length.

	f, err := os.Open(path)
	defer func() { _ = f.Close() }()
	verifyOk(err)

	h := sha256.New()
	_, err = io.Copy(h, f)
	verifyOk(err)

	dbytes := h.Sum(nil)
	return fmt.Sprintf("%x", dbytes)
}

func calculateFolderDigest(path string) string {

	dirContents, err := ioutil.ReadDir(path)
	if err != nil {
		panic(fmt.Errorf("error: %s is not a directory: %v", path, err))
	}

	// build up an alphabetically sorted list of names with their digests;
	// sorted order is delivered by ioutil.ReadDir.

	dgs := make([]string, 0)

	for _, e := range dirContents {

		name := e.Name()
		d := examinePath(filepath.Join(path, name))
		dgs = append(dgs, fmt.Sprintf("%s:%s;", name, d))
	}
	dg := ""
	for _, d := range dgs {
		dg = dg + d
	}
	return dg
}

func calculatePathDigest(path string, isRegularFile bool) string {

	if isRegularFile {
		return calculateFileDigest(path)
	} else {
		return calculateFolderDigest(path)
	}
}
