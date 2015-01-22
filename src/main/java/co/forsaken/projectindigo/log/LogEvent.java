package co.forsaken.projectindigo.log;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.gui.components.Console;

public final class LogEvent {
  public static final int CONSOLE = 0xA;
  public static final int FILE    = 0xB;
  public final LogType    type;
  public final String     body;
  public final int        meta;

  public LogEvent(LogType type, String body) {
    this(type, body, CONSOLE | FILE);
  }

  public LogEvent(LogType type, String body, int meta) {
    this.type = type;
    this.body = (!body.endsWith("\n") ? body + "\n" : body);
    this.meta = meta;
  }

  public void post(LogEventWriter writer) {
    if ((this.meta & CONSOLE) == CONSOLE) {
      Console c = IndigoLauncher._launcher.console.console;
      c.setColor(this.type.color()).setBold(true).write("[" + new Date().toLocaleString() + "] ");
      c.setColor(new Color(255, 255, 255)).setBold(false).write(this.body);
    }
    if ((this.meta & FILE) == FILE) {
      try {
        writer.write(this);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override public String toString() {
    return "[" + new Date().toLocaleString() + "] [" + this.type.name() + "]" + this.body;
  }

  public static enum LogType {
    INFO, WARN, ERROR, DEBUG;

    public Color color() {
      switch (this) {
        case INFO: {
          return new Color(137, 194, 54);
        }
        case WARN: {
          return new Color(255, 255, 76);
        }
        case ERROR: {
          return new Color(238, 34, 34);
        }
        case DEBUG: {
          return new Color(255, 0, 255);
        }
        default: {
          return new Color(255, 255, 255);
        }
      }
    }

  }
}