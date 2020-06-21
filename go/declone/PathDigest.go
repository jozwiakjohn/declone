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

func calculateFolderDigest(path string) (string, int64) {

	dirContents, err := ioutil.ReadDir(path)
	if err != nil {
		panic(fmt.Errorf("error: %s is not a directory: %v", path, err))
	}

	// build up an alphabetically sorted list of names with their digests;
	// sorted order is delivered by ioutil.ReadDir.

	dgs := make([]string, 0)
	containedsize := int64(0)

	for _, e := range dirContents {

		name := e.Name()
		d, sz := examinePath(filepath.Join(path, name))
		dgs = append(dgs, fmt.Sprintf("%s:%s;", name, d))
		containedsize += sz
	}
	dg := ""
	for _, d := range dgs {
		dg = dg + d
	}
	return dg, containedsize
}

func calculatePathDigestTypeAndSize(path string) (string, bool, int64) {

	lstat, err := os.Lstat(path)
	verifyOk(err)

	isRegularFile := lstat.Mode().IsRegular()

	var sizeAtPath int64
	var digest string

	if isRegularFile {
		digest = calculateFileDigest(path)
		sizeAtPath = lstat.Size()
	} else {
		digest, sizeAtPath = calculateFolderDigest(path)
	}

	return digest, isRegularFile, sizeAtPath
}

/*
func calculatePathSize(path string, isRegularFile bool) int64 {
	return int64(0)
}
*/
