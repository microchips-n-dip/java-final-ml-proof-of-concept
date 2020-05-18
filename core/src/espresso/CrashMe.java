package espresso;

public class CrashMe {
  public static void fatal(boolean b, String m) {
    if (!b) {
      System.out.printf("Fatal: %s\n", m);
      Thread.dumpStack();
      System.exit(-1);
    }
  }
}
