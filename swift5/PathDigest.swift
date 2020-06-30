// john jozwiak on 2020 June 25.

import Foundation
import Swift
import CryptoKit

func calculateFileDigest(_ path : String) -> String {  //  see https://www.hackingwithswift.com/example-code/cryptokit/how-to-calculate-the-sha-hash-of-a-string-or-data-instance
    
    // p names a file, so calculate a sha256 hash of it to store with its length.
    if  let nsdataFromFile = NSData.init(contentsOfFile:path) {
        let filedata = Data.init(referencing: nsdataFromFile)
        //      let size = filedata.count
        //    print("\(path) the file has \(size) bytes")
        let digest = SHA256.hash(data: filedata)
        let digesthex = digest.compactMap { String(format: "%02x", $0) }.joined()
        return "\(digesthex)"
    }
    return ""
}

func calculateFolderDigest(_ path : String) -> (digest : String, size : UInt64) {
    
    if let listing = try? filemanager.contentsOfDirectory( atPath: path ) {
        
        // build up an alphabetically sorted list of names with their digests;
        // sorted order is delivered by ioutil.ReadDir.
        
        var dgs = [String]()
        var containedsize : UInt64 = 0
        
        for e in listing {
            // print(e)
            let (d, sz) = examinePath(path + "/" + e)
            dgs.append("\(e):\(d);")
            containedsize += sz
        }
        var dg : String = ""
        for d in dgs {
            dg = dg + d
        }
        return (dg, containedsize)
    }
    
    return ("",0)
}

func calculatePathDigestTypeAndSize(path : String) -> (digest : String, isfile : Bool, size: UInt64) {
    
    var isFile : Bool = false
    var digest : String = ""
    var sizeAtPath : UInt64 = 0
    
    if filemanager.fileExists(atPath: path) {
        if let attributes = try?filemanager.attributesOfItem(atPath: path) {
            let type = attributes[.type] as! FileAttributeType
            let size = attributes[.size] as! UInt64
            isFile = (type == FileAttributeType.typeRegular)
            
            if isFile {
                // print("\(path) looks like a FILE")
                digest = calculateFileDigest(path)
                sizeAtPath = size
            } else {
                // print("\(path) looks like a FOLDER")
                (digest, sizeAtPath) = calculateFolderDigest(path)
            }
        }
    }
    return (digest,isFile,sizeAtPath)
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
