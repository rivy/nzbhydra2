package org.nzbhydra.config;

import com.google.common.base.Strings;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@ConfigurationProperties(prefix = "downloading")
public class DownloadingConfig extends ValidatingConfig<DownloadingConfig> {

    private List<DownloaderConfig> downloaders = new ArrayList<>();
    private String saveTorrentsTo;
    private boolean sendMagnetLinks;
    private boolean updateStatuses;

    @Override
    public ConfigValidationResult validateConfig(BaseConfig oldConfig, DownloadingConfig newConfig) {
        List<String> errors = new ArrayList<>();
        if (getSaveTorrentsTo().isPresent()) {
            File file = new File(getSaveTorrentsTo().get());
            if (!file.isAbsolute()) {
                errors.add("Torrent black hole folder " + getSaveTorrentsTo().get() + " is not absolute");
            }
            if (file.exists() && !file.isDirectory()) {
                errors.add("Torrent black hole folder " + file.getAbsolutePath() + " is a file");
            }
            if (!file.exists()) {
                boolean created = file.mkdir();
                if (!created) {
                    errors.add("Torrent black hole folder " + file.getAbsolutePath() + " could not be created");
                }
            }
        }
        List<ConfigValidationResult> validationResults = downloaders.stream().map(downloaderConfig -> downloaderConfig.validateConfig(oldConfig, downloaderConfig)).collect(Collectors.toList());
        List<String> downloaderErrors = validationResults.stream().map(ConfigValidationResult::getErrorMessages).flatMap(List::stream).collect(Collectors.toList());
        errors.addAll(downloaderErrors);

        List<String> warnings = validationResults.stream().map(ConfigValidationResult::getWarningMessages).flatMap(List::stream).collect(Collectors.toList());

        return new ConfigValidationResult(errors.isEmpty(), false, errors, warnings);
    }

    public Optional<String> getSaveTorrentsTo() {
        return Optional.ofNullable(Strings.emptyToNull(saveTorrentsTo));
    }

    @Override
    public DownloadingConfig prepareForSaving() {
        return this;
    }

    @Override
    public DownloadingConfig updateAfterLoading() {
        return this;
    }

}
