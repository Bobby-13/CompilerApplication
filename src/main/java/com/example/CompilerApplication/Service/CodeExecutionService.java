package com.example.CompilerApplication.Service;

import com.example.CompilerApplication.DTO.CodeExecutionRequest;
import com.example.CompilerApplication.DTO.CodeExecutionResult;
import com.example.CompilerApplication.DTO.CodeFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CodeExecutionService {

    @Autowired
    private CodeFileManager codeFileManager;

    public static final List<String> supportedLanguages = Arrays.asList("java", "cpp", "py", "c", "js", "go");

    public CodeExecutionResult runCode(CodeExecutionRequest request, String id) throws Exception {
        String language = request.getLanguage();
        String code = request.getCode();
        String input = request.getInput();

        validateRequest(language, code);
        System.out.println("AfterValidation");

        CodeFile codeFile = codeFileManager.createCodeFile(language, code, id);
        System.out.println("code file :" + codeFile);
        CodeExecutionResult result = executeCode(language, codeFile, input);

        try {
            System.out.println("After code Execution result : " + result);
            return result;
        }
        finally {
            codeFileManager.removeCodeFile(codeFile.getJobID(), language, getOutputExtension(language));
        }
    }

    private void validateRequest(String language, String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("No code found to execute.");
        }

        if (!supportedLanguages.contains(language)) {
            throw new IllegalArgumentException("Please enter a valid language.");
        }
    }

    public CodeExecutionResult executeCode(String language, CodeFile codeFile, String input) throws Exception {
        String jobID = codeFile.getJobID();
        String codeFilePath = codeFile.getFilePath();
        String outputExt = getOutputExtension(language);

        System.out.println("job Id :" + jobID);
        System.out.println("codeFile path :" + codeFilePath);
        String executeCommand;
        String[] executionArgs;
        String compiledBinaryPath = getOutputFilePath(jobID, outputExt);
        switch (language) {
            case "java":
                executeCommand = "java";
                executionArgs = new String[]{codeFilePath};
                break;
            case "cpp":
                executeCommand = "g++";
                executionArgs = new String[]{codeFilePath, "-o", compiledBinaryPath};
                String formattedCommand = String.join(" ", executionArgs);
                System.out.println("Formatted Command: " + formattedCommand);
                break;
            case "py":
                executeCommand = "python3";
                executionArgs = new String[]{codeFilePath};
                break;
            case "c":
                executeCommand = "gcc";
                executionArgs = new String[]{codeFilePath, "-o", compiledBinaryPath};
                System.out.println("Formatted C Path :" + (String.join(" ", executionArgs)));
                System.out.println(getOutputFilePath(jobID, outputExt));
                break;
            case "js":
                executeCommand = "node";
                executionArgs = new String[]{codeFilePath};
                break;
            case "go":
                executeCommand = "go";
                executionArgs = new String[]{"run", codeFilePath};
                break;
            default:
                throw new UnsupportedOperationException("Language not supported: " + language);
        }

        long startTime = System.currentTimeMillis();
        CodeExecutionResult result = new CodeExecutionResult();
        try {
            if (executeCommand.equals("g++") || executeCommand.equals("gcc")) {
                String command = "chmod +x " + codeFilePath;
                Process process3 = Runtime.getRuntime().exec(command);
                process3.waitFor();

                List<String> cmd;

                cmd = Arrays.asList(executeCommand, codeFilePath, "-o", compiledBinaryPath);


                ProcessBuilder compilationProcessBuilder = new ProcessBuilder(cmd);
                Process compilationProcess = compilationProcessBuilder.start();
                compilationProcess.waitFor();

                long compilationTime = System.currentTimeMillis() - startTime;
                System.out.println("Compilation Time: " + compilationTime + " milliseconds");

                // Measure storage capacity after compilation
                Path compiledPath = Paths.get(getOutputFilePath(jobID, outputExt));
                long compiledBinarySize = compiledPath.toFile().length();
                System.out.println("Compiled Binary Size: " + compiledBinarySize + " bytes");

                // Measure storage capacity after execution
                long outputSize = compiledPath.toFile().length();
                System.out.println("Output Size: " + outputSize + " bytes");

            if (compilationProcess.exitValue() == 0) {
                String chmodCommand = "chmod +x " + compiledBinaryPath;
                Process chmodProcess = Runtime.getRuntime().exec(chmodCommand);
                chmodProcess.waitFor();
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;
                result.setExeTime(executionTime+ " milliseconds");
                System.out.println("Execution Time: " + executionTime + " milliseconds");
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(compilationProcess.getErrorStream()));
                String line;
                StringBuilder errorMessage = new StringBuilder("Compilation Error:\n");
                while ((line = errorReader.readLine()) != null) {
                    errorMessage.append(line).append("\n");
                }

                String regex = "codes/[a-zA-Z0-9-]+\\.";
                String res = errorMessage.toString().replaceAll(regex, "Main.");
                result.setError(res);
                return result;
            }

                System.out.println("After Compilation ::");

                    ProcessBuilder processBuilder1 = new ProcessBuilder(compiledBinaryPath);

                    Process process1 = processBuilder1.start();
                    if (input != null && !input.isEmpty()) {
                        try (OutputStream outputStream = process1.getOutputStream()) {
                            outputStream.write(input.getBytes());
                            System.out.println("Input field :" + input.getBytes());
                        }
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process1.getInputStream()));
                    StringBuilder output = new StringBuilder();

                    Thread timeoutThread = new Thread(() -> {
                        try {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println("Output block : ");
                                output.append(line).append("\n");
                                System.out.println("-->" + output);
                            }
                            process1.waitFor();
                            long endTime = System.currentTimeMillis();
                            long executionTime = endTime - startTime;
                            result.setExeTime(executionTime + " milliseconds");

                            System.out.println("Execution Time: " + executionTime + " milliseconds");

                        }  catch (InterruptedException | IOException e) {
                            result.setError("Runtime Exceeded");
                            throw new RuntimeException("RunTime Exceeded");
                        }
                    });

                    timeoutThread.start();
                    timeoutThread.join(1 * 1000);
                if (timeoutThread.isAlive()) {
                    timeoutThread.interrupt();
                    process1.destroy();
                    throw new RuntimeException("Process execution exceeded time limit");
                }
                    System.out.println("Code Executed :");
                  result.setOutput(output.toString());
                    return result;



            } else {
                ProcessBuilder processBuilder = new ProcessBuilder();
                List<String> commandList = new ArrayList<>();
                commandList.add(executeCommand);
                commandList.addAll(Arrays.asList(executionArgs));
                processBuilder.command(commandList);
                processBuilder.directory(new File(System.getProperty("user.dir")));
                processBuilder.redirectErrorStream(true);

                Process process = processBuilder.start();
                System.out.println(process.getOutputStream());
                if (input != null && !input.isEmpty()) {
                    process.getOutputStream().write(input.getBytes());
                    process.getOutputStream().close();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();

                Thread timeoutThread = new Thread(() -> {
                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("Output block : ");
                            output.append(line).append("\n");
                            System.out.println(output);
                            long outputSize = output.toString().getBytes().length;
                        }
                        process.waitFor();
                        long endTime = System.currentTimeMillis();
                        long executionTime = endTime - startTime;
                        result.setExeTime(executionTime + " milliseconds");
                        System.out.println("Execution Time: " + executionTime + " milliseconds");
                    } catch (InterruptedException | IOException e) {
                        result.setError("Runtime Exceeded");
                        throw new RuntimeException("RunTime Exceeded");
                    }
                });

                timeoutThread.start();
                timeoutThread.join(1 * 1000);
                if (timeoutThread.isAlive()) {
                    timeoutThread.interrupt();
                    process.destroy();
                    throw new RuntimeException("Process execution exceeded time limit");
                }


                if(output.toString().contains("codes/")){

                    if(language.equals("py")){
                        String regex = "File \".*\\.py\"";
                        String res = output.toString().replaceAll(regex, "Main.py");
                        result.setError(res);
                    }else {
                        String regex = "codes/[a-zA-Z0-9-]+\\.";
                        String res = output.toString().replaceAll(regex, "Main.");
                        result.setError(res);
                    }
                }else {
                    result.setOutput(output.toString());
                }

                String k = codeFile.getFileName().replaceAll("\\.\\w+", "");

                return result;
            }
        } catch (Exception e) {
            throw new Exception(e.toString());
        }
    }

        private String getOutputExtension (String language){
            switch (language) {
                case "java":
                case "js":
                case "go":
                    return ".out";
                case "cpp":
                case "c":
                case "py":
                    return "";

                default:
                    throw new UnsupportedOperationException("Language not supported: " + language);
            }
        }

        private String getOutputFilePath (String jobID, String outputExt){
            return Paths.get("outputs", jobID + "" + outputExt).toString();
        }
    }

