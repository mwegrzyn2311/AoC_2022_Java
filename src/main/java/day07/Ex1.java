package day07;

import common.FileParser;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Ex1 {
    private static final String CMD = "cmd";
    private static final String COMMAND_FORMAT_NO_ARGS = format("\\$ (?<%s>\\w+)", CMD);
    private static final String ARG = "arg";
    private static final String COMMAND_FORMAT_WITH_ARG = format("\\$ (?<%s>\\w+) (?<%s>[\\w\\/\\.]+)", CMD, ARG);
    private static final String NAME = "name";
    private static final String DIR_FORMAT = format("dir (?<%s>\\w+)", NAME);
    private static final String SIZE = "size";
    private static final String FILE_FORMAT = format("(?<%s>\\d+) (?<%s>[\\w\\.]+)", SIZE, NAME);

    private static final Pattern COMMAND_NO_ARGS_PATTERN = Pattern.compile(COMMAND_FORMAT_NO_ARGS);
    private static final Pattern COMMAND_WITH_ARGS_PATTERN = Pattern.compile(COMMAND_FORMAT_WITH_ARG);
    private static final Pattern DIR_PATTERN = Pattern.compile(DIR_FORMAT);
    private static final Pattern FILE_PATTERN = Pattern.compile(FILE_FORMAT);

    private static final long UNDER = 100000L;

    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input07.txt");

        Directory root = new Directory(null, "/");
        Directory currDir = root;

        for (String line : input) {
            Matcher matcher = COMMAND_NO_ARGS_PATTERN.matcher(line);
            if (matcher.matches()) {
                String cmd = matcher.group(CMD);
                continue;
            }
            matcher = COMMAND_WITH_ARGS_PATTERN.matcher(line);
            if (matcher.matches()) {
                String cmd = matcher.group(CMD);
                String arg = matcher.group(ARG);
                if (cmd.equals("cd")) {
                    switch (arg) {
                        case "/":
                            currDir = root;
                            break;
                        default:
                            currDir = currDir.cd(arg);
                    }
                }
                continue;
            }
            matcher = DIR_PATTERN.matcher(line);
            if (matcher.matches()) {
                String name = matcher.group(NAME);
                currDir.addDir(new Directory(currDir, name));
                continue;
            }
            matcher = FILE_PATTERN.matcher(line);
            if (matcher.matches()) {
                String name = matcher.group(NAME);
                long size = Long.parseLong(matcher.group(SIZE));
                currDir.addFile(new File(currDir, name, size));
                continue;
            }
            throw new Exception(format("Unhandled line: %s", line));
        }

        System.out.println(root.getSumOfDirsOfAtMostSize(UNDER));
    }

    static abstract class SystemElement {
        protected long size = -1L;
        protected final String name;
        protected final Directory parent;

        protected SystemElement(Directory parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public abstract long getSize();
    }

    static class Directory extends SystemElement {
        private List<Directory> directories = new LinkedList<>();
        private List<File> files = new LinkedList<>();

        public Directory(Directory parent, String name) {
            super(parent, name);
        }

        public void addDir(Directory dir) {
            this.directories.add(dir);
        }

        public void addFile(File file) {
            this.files.add(file);
        }

        public Directory cd(String name) {
            if (name.equals("..")) {
                return this.parent;
            } else {
                for (Directory element : this.directories) {
                    if (element.getName().equals(name)) {
                        return element;
                    }
                }
            }
            return null;
        }

        private void calculateSize() {
            long filesSize = files.stream()
                    .mapToLong(SystemElement::getSize)
                    .sum();
            long dirsSize = directories.stream()
                    .mapToLong(SystemElement::getSize)
                    .sum();

            this.size = filesSize + dirsSize;
        }

        public long getSumOfDirsOfAtMostSize(long atMost) {
            return directories.stream()
                    .mapToLong(dir -> dir.getSumOfDirsOfAtMostSize(atMost))
                    .sum() + (this.getSize() <= atMost ? this.size : 0L);
        }

        @Override
        public long getSize() {
            if (this.size == -1L) {
                this.calculateSize();
            }

            return this.size;
        }
    }

    static class File extends SystemElement {
        public File(Directory parent, String name, long size) {
           super(parent, name);
           this.size = size;
        }

        @Override
        public long getSize() {
            return this.size;
        }
    }
}
