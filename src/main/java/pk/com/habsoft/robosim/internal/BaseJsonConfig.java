package pk.com.habsoft.robosim.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * BaseJsonConfig class is base class of all json base configurations. Concrete
 * configuration classes need to extend this class to add functionality of
 * properties marshalling/unmarshalling.
 * <p>
 * All properties of child classes will be saved in file in json format.
 *
 * @author faisal-hameed
 * @param <T>
 *            The class type of child class
 */
public abstract class BaseJsonConfig<T> {

	/** The file name of property file. */
	protected String fileName;

	/** The clazz. */
	protected Class<T> clazz;

	/**
	 * Instantiates a new base json config.
	 *
	 * @param file
	 *            the file
	 * @param claaz
	 *            the claaz
	 */
	protected BaseJsonConfig(String file, Class<T> claaz) {
		this.fileName = file;
		this.clazz = claaz;
	}

	/**
	 * Save configuration.
	 */
	public void saveConfiguration() {
		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(new FileWriter(fileName), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load existing/default configuration.
	 *
	 * @return the t
	 */
	public T loadConfiguration() {
		ObjectMapper mapper = new ObjectMapper();
		T config = null;
		try {
			config = mapper.readValue(new File(fileName), clazz);
		} catch (IOException e) {
			config = loadDefault();
		}
		return config;
	}

	/**
	 * Load default configuration defined by child classes.
	 *
	 * @return the configuration
	 */
	public abstract T loadDefault();

}
