# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # All Vagrant configuration is done here. The most common configuration
  # options are documented and commented below. For a complete reference,
  # please see the online documentation at vagrantup.com.

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box = "hashicorp/precise64"
  config.vm.hostname = "vagrant"
  config.vm.network "forwarded_port", guest: 5432, host: 5432

  config.vm.provision :shell, :path => "server-env/upgrade_puppet.sh"
  config.vm.provision "puppet" do |puppet|
    puppet.manifests_path = "server-env/manifests"
    puppet.module_path = "server-env/modules"
    puppet.manifest_file  = "site.pp"
  end

end
