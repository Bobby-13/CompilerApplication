package com.example.CompilerApplication.Controller;


import com.example.CompilerApplication.DTO.CodeExecutionRequest;
import com.example.CompilerApplication.DTO.CodeExecutionResult;
import com.example.CompilerApplication.Service.CodeExecutionService;
import com.example.CompilerApplication.Service.CodeFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CodeExecutionController {

    @Autowired
    private CodeFileManager codeFileManager;

    @Autowired
    private CodeExecutionService codeExecutionService;

    @GetMapping("/execution{id}")
    public ResponseEntity<?> runCode(@RequestBody CodeExecutionRequest request, @PathVariable String id) {
        try {
            CodeExecutionResult result = codeExecutionService.runCode(request,id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println(e);
            CodeExecutionResult codeExecutionResult = CodeExecutionResult.builder().error(e.toString().replaceAll(".*\\: ","")).build();
            return ResponseEntity.ok(codeExecutionResult);
        }
    }


    @PostMapping("/codeSubmission/{id}")
    public LinkedHashMap<Integer,String> submitCode(@RequestBody CodeExecutionRequest request, @PathVariable String id){
        try {

            List<String> inputs = new ArrayList<>();
            inputs.add("5\n2");


            String arr[] = {"5\n4","5\n1","2\n2","7\n8","9\n0","12\n4","3\n4","2\n234","12\n4","5\n67"};

            inputs.addAll(Arrays.stream(arr).toList());

            String output[] = {"7.0\n","9.0\n","5.0\n","4.0\n","15.0\n","9.0\n","12.0\n","7.0\n","34.0\n","16.0\n","72.0\n"};
            List<String> outputs = new ArrayList<>();
            outputs.addAll(Arrays.stream(output).toList());
            //"5\n2","5\n2","5\n2","5\n2","5\n2","5\n2","5\n2","5\n2","5\n2","5\n2"

//            CodeExecutionResult result = codeExecutionService.runCode(request,id);
//            return result;
            LinkedHashMap<Integer,String> op = new LinkedHashMap<>();

            for(int i=0;i<inputs.size();i++){
                request.setInput(inputs.get(i));
                CodeExecutionResult result = codeExecutionService.runCode(request,id);
                System.out.println("=========================================================================>>>"+result.getOutput());
                if(result.getOutput().equals(outputs.get(i))){
                    op.put(i,"PASSED");
                }
                else{
                    op.put(i,"FAILED");
                }
            }


            return op;
        } catch (Exception e) {
            System.out.println(e);
//            CodeExecutionResult codeExecutionResult = handleException(e);
//            return CodeExecutionResult.builder().error(e.toString()).exeTime("TimeLimit Exceeded").build();
            LinkedHashMap<Integer,String> err = new LinkedHashMap<>();
            err.put(0,e.toString());
            return err;
        }
    }
}

