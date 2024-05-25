import java.io.*;
import java.text.*;
import java.util.*;

public class ExecutionTime {

    public static void main(String[] args) {
        String logFilePath = "C:\\Users\\Austin\\Desktop\\extracted_log";
        List<Integer> Ids = new ArrayList<>();
        List<Date> StartTimes = new ArrayList<>();
        List<Date> EndTimes = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("_slurm_rpc_submit_batch_job")) {
                    int jobId = extractId(line);
                    Date startTime = Times(line, dateFormat);
                    Ids.add(jobId);
                    StartTimes.add(startTime);
                    EndTimes.add(null); // Placeholder for end time
                } else if (line.contains("job_complete")) {
                    int jobId = extractId(line);
                    Date endTime = Times(line, dateFormat);
                    int index = Ids.indexOf(jobId);
                    if (index != -1) {
                        EndTimes.set(index, endTime);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        long totalExecutionTime = 0;
        int Count = 0;

        for (int i = 0; i < Ids.size(); i++) {
            Date startTime = StartTimes.get(i);
            Date endTime = EndTimes.get(i);

            if (endTime != null) {
                long executionTime = endTime.getTime() - startTime.getTime();
                totalExecutionTime += executionTime;
                Count++;
            }
        }

        if (Count > 0) {
            long averageExecutionTime = totalExecutionTime / Count;
            System.out.println("Average Execution Time: " + averageExecutionTime + " milliseconds");
        } else {
            System.out.println("No jobs found.");
        }
    }

    private static int extractId(String logLine) {
        String[] parts = logLine.split(" ");
        for (String part : parts) {
            if (part.startsWith("JobId=")) {
                return Integer.parseInt(part.substring(6));
            }
        }
        return -1;
    }

    private static Date Times(String logLine, SimpleDateFormat dateFormat) throws ParseException {
        String timestampStr = logLine.substring(1, 24);
        return dateFormat.parse(timestampStr);
    }
}