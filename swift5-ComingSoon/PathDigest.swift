// john jozwiak on 2020 June 25.

import Foundation
import Swift

func calculateFileDigest(path : String) -> String {
    
    // p names a file, so calculate a sha256 hash of it to store with its length.
    
    let filedata = NSData.init(contentsOfFile:path)
    let size = filedata!.count
    print("\(path) the file has \(size) bytes")

    return "\(path)\(size)"
    
    //	f, err := os.Open(path)
    //	defer func() { _ = f.Close() }()
    //	verifyOk(err)
    
    //	h := sha256.New()
    //	_, err = io.Copy(h, f)
    //	verifyOk(err)
    
    //	dbytes := h.Sum(nil)
    //	return fmt.Sprintf("%x", dbytes)
}

func calculateFolderDigest(path : String) -> (digest : String, size : Int64) {
    
    //        let listing = try! filemanager.contentsOfDirectory( atPath: arg )
    //        for o in listing {
    //            print(o)
    //        }
    
    return ("",0)
    /*
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
     */
}

func calculatePathDigestTypeAndSize(path : String) -> (digest : String, isfile : Bool, size: Int64) {
    
    return ("",true,0)
    /*
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
     
     return (digest, isRegularFile, sizeAtPath)
     */
}

func normalizePath( _ path : String ) -> String {
    return path // obviously not what is intended, more like Go's path/filepath.Clean().
}

func TestNormalizePath() {  //  this function exists to verify i use stdlib filepath.Clean properly
    let problemsAndAnswers : [String:String] = [
        "a//b" : "a/b",
        "."    : ".",
        "a/.." : ".",
    ]
    for (x, fx) in problemsAndAnswers {
        let n = normalizePath(x)
        if n != fx {
            print("mismatch in normalizing \(x) -> normalized = \(n) which is not expected \(fx)")
        }
    }
}
