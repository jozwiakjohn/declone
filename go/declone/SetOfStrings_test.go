package main


import "testing"


func TestInsert(t *testing.T) {

  s := make(SetOfString)
  insertWorks := s.Insert("a").Contains("a")
  if !insertWorks {
    t.Errorf( "insert failed, as assessed by contains" )
  }
}


func TestDelete(t *testing.T) {

  s := make(SetOfString)
  s = s.Insert("a")
  insertWorked := s.Contains("a")
  s = s.Remove("a")
  deleteWorked := ! s.Contains("a")

  if !( insertWorked && deleteWorked) {
    t.Errorf( "insert followed by deleted failed, as assessed by contains" )
  }
}


func TestContains( t *testing.T ) {

  TestInsert(t)
}

