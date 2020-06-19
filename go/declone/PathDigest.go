package main

import (
	"crypto/sha256"
	"io"
	"os"
)

func calculateFileDigest(path string) []byte {

	// p names a file, so calculate a sha256 hash of it to store with its length.

	f, err := os.Open(path)
	defer func() { _ = f.Close() }()
	verifyOk(err)

	h := sha256.New()
	_, err = io.Copy(h, f)
	verifyOk(err)

	return h.Sum(nil)
}

