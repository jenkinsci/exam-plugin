package jenkins.task.TestUtil;

import hudson.console.ConsoleNote;
import hudson.model.TaskListener;
import hudson.remoting.Channel;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class FakeTaskListener implements TaskListener {
    @Nonnull @Override public PrintStream getLogger() {
        return null;
    }

    @Nonnull @Override public Charset getCharset() {
        return null;
    }

    @Override public PrintWriter _error(String prefix, String msg) {
        return null;
    }

    @Override public void annotate(ConsoleNote ann) throws IOException {

    }

    @Override public void hyperlink(String url, String text) throws IOException {

    }

    @Nonnull @Override public PrintWriter error(String msg) {
        return null;
    }

    @Nonnull @Override public PrintWriter error(String format, Object... args) {
        return null;
    }

    @Nonnull @Override public PrintWriter fatalError(String msg) {
        return null;
    }

    @Nonnull @Override public PrintWriter fatalError(String format, Object... args) {
        return null;
    }

    @Nonnull @Override public Channel getChannelForSerialization() throws NotSerializableException {
        return null;
    }
}
