// john jozwiak on 2020 June 25

import Foundation
import Swift

func TestNormalizePath() {  //  this function exists to verify i use stdlib filepath.Clean properly
    let problemsAndAnswers : [String:String] = [
		"a//b" : "a/b",
		"."    : ".",
		"a/.." : ".",
	]
	for in, out = range problemsAndAnswers {
		n := filepath.Clean(in)
		if n != out {
			print("mismatch in normalizing \(in) -> normalized = \(n) which is not expected \(out)")
		}
	}
}
