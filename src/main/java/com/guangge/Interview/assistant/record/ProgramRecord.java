package com.guangge.Interview.assistant.record;

public record ProgramRecord(String question, exampleRecord example, String code) {
}

record exampleRecord(String input,String output){

}
