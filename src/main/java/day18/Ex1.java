package day18;

import common.FileParser;
import common.Vec3d;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ex1 {
    public static void main(String[] args) throws Exception {
        List<String> input = FileParser.inputToStrList("/input18.txt");

        LavaDroplets lavaDroplets = new LavaDroplets();

        for (String line : input) {
            String[] xyz = line.split(",");
            lavaDroplets.addCube(new Vec3d(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])));
        }

        System.out.println(lavaDroplets.getFaces());
    }

    static class LavaDroplets {
        private final Set<Vec3d> positions = new HashSet<>();
        private int faces = 0;

        public void addCube(Vec3d pos) {
            faces += (6 - countAdjacent(pos) * 2);
            positions.add(pos);
        }

        private long countAdjacent(Vec3d pos) {
            return positions.stream()
                    .filter(occupied -> occupied.isAdjacent(pos))
                    .count();
        }

        public int getFaces() {
            return  this.faces;
        }
    }
}
